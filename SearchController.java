package egovframework.search.controller;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import egovframework.search.vo.ESResult;
import egovframework.search.vo.IrisnetSearchVO;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

/*****************************************************
 * @title : Search middle ware 
 * @author : tintwo
 * @usage : main search, stnd search, terms search
 *****************************************************
 *		일자			작성자	내용
 *****************************************************
 *  2023-01-4		이주석	검색 전용 미들웨어
 *****************************************************/

@Controller
public class SearchController {
	
	// main search 
	// param mode, keyword 
	// mode all = 전체
	// stnd = 공간정보표준
	// terms = 공간정보표준용어 
	
	@RequestMapping(value ="main/search.do", method=RequestMethod.GET)
	public List< Map<String,String> > getMainSearch(@RequestParam(value="mode", defaultValue="all") String mode, @RequestParam(value="keyword", defaultValue="") String keyword) throws UnsupportedEncodingException{
		
		final String [] stndKeys = {"seq", "stType", "stNum", "stNmKor", "stNmEng", "coverage", "eblshDt", "finalRvisnDt" };
		
		final String [] termsKeys = {"termsMainId", "termsNo", "kornTerms", "engTerms", "mdfcnYmd", "termsDfn", "nrmtvRefNo"};
		
		if("stnd".equals(mode)){
			
			// elasticsearch 연결 설정
			Settings SETTINGS = Settings.builder().put("cluster.name", "exlis_app")
					.put("client.transport.sniff", true)
					.put("node.name", "exlis")
					.put("network.tcp.blocking", false) // tcp non-blocking mode
					.put("client.transport.ping_timeout", "10s").build();

			PreBuiltTransportClient client = new PreBuiltTransportClient(SETTINGS);

			try {
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.0.141"), 9300));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			// request용 vo
			IrisnetSearchVO searchVO = new IrisnetSearchVO();
			searchVO.setFirstRecordIndex(0);
			searchVO.setPageSize(100);
			searchVO.setKwd(keyword);
			
			
			// index명 설정
			SearchRequestBuilder searchRequest = client.prepareSearch("std-search").setSearchType(SearchType.DEFAULT);
			
			searchRequest.setSize( searchVO.getPageSize() );
			searchRequest.setFrom( searchVO.getFirstRecordIndex() );
			
			// 정렬 설정
			BoolQueryBuilder qb = buildSearchQuery(searchVO);
			if( StringUtils.isEmpty( searchVO.getSort() ) ){
				searchRequest.addSort(SortBuilders.fieldSort("RECDT").order(SortOrder.DESC));
				//searchRequest.addSort(SortBuilders.scoreSort().order(SortOrder.DESC));
			}else{
				if("RECDT".equals(searchVO.getSort())){
					searchRequest.addSort(SortBuilders.fieldSort(searchVO.getSort()).order(SortOrder.DESC));

				}else{
					searchRequest.addSort(SortBuilders.fieldSort(searchVO.getSort()).order(SortOrder.ASC));
				}
			}

			if (qb != null) {
				searchRequest.setQuery(qb);
			}

			//패싯 - 집합함수
			searchRequest.addAggregation(AggregationBuilders.terms("STDIV_FACET").field("STDIV.keyword").order(Terms.Order.term(true)).size(9999));
			/*searchRequest.addAggregation(AggregationBuilders.terms("mtrSourceNmFacet").field("mtrSourceNm").order(Terms.Order.term(true)).size(9999));
			searchRequest.addAggregation(AggregationBuilders.terms("techPftCdFacet").field("techPftCd").order(Terms.Order.term(true)).size(9999));*/
			
			//하이라이트
			HighlightBuilder highlightBuilder = new HighlightBuilder();

			highlightBuilder.preTags("<em class='highlight'>");
			highlightBuilder.postTags("</em>");
			highlightBuilder.field("COVERAGE");
			highlightBuilder.field("STNMKOR");
			
			searchRequest.highlighter(highlightBuilder);
			
//			System.out.println("######## search Request ########");
//			System.out.println(searchRequest.toString());
			
			// 검색 요청
			ListenableActionFuture<SearchResponse> response = searchRequest.execute();
			
			// 결과 파싱
			SearchResponse r = null;
			Map<String,Object> rtn ;
			try{
				
				r = response.actionGet();
				rtn = parseResult(searchVO, r);
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement je = JsonParser.parseString(rtn.toString());
				
				String prettyJsonString = gson.toJson(je);
				
//				String seq = je.getAsJsonObject().get("seq").getAsString();
//				System.out.println(seq);
				
				
//				System.out.println("######## prettyJsonString ########");
//				System.out.println(prettyJsonString);
				
			}catch(Exception e){
				rtn = new HashMap<String, Object>();
				rtn.put("exceptionMessage", e.getMessage());
				rtn.put("exceptionCode", e.getCause());
			}
			// 표준 검색
			
			List<Map<String,String>> stnd = new ArrayList();
			
			
			Map<String,String> article = new HashMap<>();
			
			for(int i = 0; i<10; i++){
				
				for(int j = 0; j<stndKeys.length; j++){
					article.put("mode", "stnd");
					article.put(stndKeys[j], "test "+j + " ");
				}
				
				stnd.add(article);
			}
			
			for(Map<String,String> articles : stnd){
				System.out.println("#################### STND ####################");
				System.out.println("#################### mode: "+ articles.get("mode"));
				System.out.println("#################### seq: "+ articles.get("seq"));
				System.out.println("#################### stType: "+ articles.get("stType"));
				System.out.println("#################### stNum: "+ articles.get("stNum"));
				System.out.println("#################### stNmKor: "+ articles.get("stNmKor"));
				System.out.println("#################### stNmEng: "+ articles.get("stNmEng"));
				System.out.println("#################### coverage: "+ articles.get("coverage"));
				System.out.println("#################### eblshDt: "+ articles.get("eblshDt"));
				System.out.println("#################### finalRvisnDt: "+ articles.get("finalRvisnDt"));
				System.out.println();
			}
			return stnd;
		}else if("terms".equals(mode)){
			// 용어 검색
			
			List<Map<String,String>> terms = new ArrayList();
			
			Map<String,String> article = new HashMap<>();
			
			for(int i = 0; i<10; i++){
				
				for(int j = 0; j<=termsKeys.length; j++){
					article.put("mode", "terms");
					article.put(termsKeys[j], "test "+j + " ");
				}
				
				terms.add(article);
			}
			for(Map<String,String> articles : terms){
				System.out.println("#################### TERMS ####################");
				System.out.println("#################### mode: "+ articles.get("mode"));
				System.out.println("#################### termsMainId: "+ articles.get("termsMainId"));
				System.out.println("#################### termsNo: "+ articles.get("termsNo"));
				System.out.println("#################### kornTerms: "+ articles.get("kornTerms"));
				System.out.println("#################### engTerms: "+ articles.get("engTerms"));
				System.out.println("#################### mdfcnYmd: "+ articles.get("mdfcnYmd"));
				System.out.println("#################### termsDfn: "+ articles.get("termsDfn"));
				System.out.println("#################### nrmtvRefNo: "+ articles.get("nrmtvRefNo"));
				System.out.println();
			}
			
			return terms;
		}else{
			// 전체 검색
			
			// stnd 
			List<Map<String,String>>  all = new ArrayList();
			
			Map<String,String> stndArticle = new HashMap<>();
			
			for(int i = 0; i<10; i++){
				
				for(int j = 0; j<stndKeys.length; j++){
					stndArticle.put("mode", "stnd");
					stndArticle.put(stndKeys[j], "test "+j + " ");
				}
				
				all.add(stndArticle);
			}
			
			// terms
			Map<String,String> termsArticle = new HashMap<>();
			
			for(int i = 0; i<10; i++){
				
				for(int j = 0; j<termsKeys.length; j++){
					termsArticle.put("mode", "terms");
					termsArticle.put(termsKeys[j], "test "+j + " ");
				}
				
				all.add(termsArticle);
			}
			
			
			// log 
			for(Map<String,String> articles : all){
				String division = articles.get("mode");
				
				if("stnd".equals(division)){
					
					// stnd 일 때 
					System.out.println("#################### STND ####################");
					System.out.println("#################### mode: "+ articles.get("mode"));
					System.out.println("#################### seq: "+ articles.get("seq"));
					System.out.println("#################### stType: "+ articles.get("stType"));
					System.out.println("#################### stNum: "+ articles.get("stNum"));
					System.out.println("#################### stNmKor: "+ articles.get("stNmKor"));
					System.out.println("#################### stNmEng: "+ articles.get("stNmEng"));
					System.out.println("#################### coverage: "+ articles.get("coverage"));
					System.out.println("#################### eblshDt: "+ articles.get("eblshDt"));
					System.out.println("#################### finalRvisnDt: "+ articles.get("finalRvisnDt"));
					System.out.println();
					
				}else if("terms".equals(division)){ 
					
					// terms 일 때 
					System.out.println("#################### TERMS ####################");
					System.out.println("#################### mode: "+ articles.get("mode"));
					System.out.println("#################### termsMainId: "+ articles.get("termsMainId"));
					System.out.println("#################### termsNo: "+ articles.get("termsNo"));
					System.out.println("#################### kornTerms: "+ articles.get("kornTerms"));
					System.out.println("#################### engTerms: "+ articles.get("engTerms"));
					System.out.println("#################### mdfcnYmd: "+ articles.get("mdfcnYmd"));
					System.out.println("#################### termsDfn: "+ articles.get("termsDfn"));
					System.out.println("#################### nrmtvRefNo: "+ articles.get("nrmtvRefNo"));
					System.out.println();
				}
				
			}
			
			return all;

		}
		
	}// getMainSearch
	
	
	/**
	 * 검색엔진 쿼리 빌드
	 * @param searchVO
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static BoolQueryBuilder buildSearchQuery(IrisnetSearchVO searchVO) throws UnsupportedEncodingException{

		BoolQueryBuilder qb = new BoolQueryBuilder();

		String keyword = searchVO.getKwd();

		if( keyword != null && !"".equals(keyword) ){

			BoolQueryBuilder keywordBool = new BoolQueryBuilder();

				String field = searchVO.getSrchFd();

				if( StringUtils.isEmpty(field) ){

					keywordBool.must( QueryBuilders.matchQuery("_all", keyword) );
					//keywordBool.should( QueryBuilders.matchQuery("itgrtTechNm", keyword).boost(3f) );

				}else if( "ITGRT_TECH_NM".equalsIgnoreCase(field) ){

					keywordBool.must( QueryBuilders.matchQuery("itgrtTechNm", keyword).operator(Operator.AND) );

				}
				

			qb.must(keywordBool);

		}

		// 제공기관
		if( searchVO.getMtrSourceNm() != null && searchVO.getMtrSourceNm().length > 0 && searchVO.getMtrSourceNm()[0] != null ){

			BoolQueryBuilder keywordBool = new BoolQueryBuilder();
			keywordBool.minimumShouldMatch(1);

			for(String mtrSource : searchVO.getMtrSourceNm() ){

				keywordBool.should( QueryBuilders.termQuery("mtrSourceNm", mtrSource ) );

			}

			qb.must(keywordBool);

		}

		//국제 특허 분류
		if( !StringUtils.isEmpty( searchVO.getIpcCd() ) ){

			BoolQueryBuilder keywordBool = new BoolQueryBuilder();
			keywordBool.minimumShouldMatch(1);
			keywordBool.should( QueryBuilders.wildcardQuery("ipcCd", searchVO.getIpcCd()+"*" ) );

			qb.must(keywordBool);

		}

		// 기술성숙도
		if( !StringUtils.isEmpty( searchVO.getTechPftFacet() ) ){
			qb.filter(QueryBuilders.termQuery("techPftCd", searchVO.getTechPftFacet()));
		}

		// 패싯 특허분류 선택
		if( !StringUtils.isEmpty( searchVO.getIpcTypeFacet() ) ){
			qb.filter(QueryBuilders.termQuery("ipcType", searchVO.getIpcTypeFacet()));
		}

		// 패싯 제공기관 선택
		if( !StringUtils.isEmpty( searchVO.getMtrSourceNmFacet() ) ){
			qb.filter(QueryBuilders.termQuery("mtrSourceNm", searchVO.getMtrSourceNmFacet()));
		}

		return qb;

	}
	
	
	/**
	 * 검색엔진 결과 -> DB결과 맵핑
	 * 같은 UI사용하기 위해 VO 변환
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseResult(IrisnetSearchVO searchVO, SearchResponse response) throws Exception {
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement je = JsonParser.parseString(response.toString());
		String prettyJsonString = gson.toJson(je);

		System.out.println("######## prettyJsonString ########");
		System.out.println(prettyJsonString);

		Map<String, Object> rtn = new HashMap<String, Object>();
		SearchHits hits = response.getHits();
		SearchHit[] hitArr = hits.internalHits();

		
		List<ESResult> hitInfos = new ArrayList<ESResult>();
		
		int idx = searchVO.getFirstRecordIndex()+1;
		for(SearchHit h : hitArr){

			Map<String, Object> fields = h.getSource();
			Map<String, HighlightField> hl_fields = h.getHighlightFields();
			
			ESResult esRes = new ESResult();
			esRes.setHitList(fields);
			esRes.setHlList(hl_fields);
			
			fields.put("listNum", idx);

			hitInfos.add(esRes);
			
			rtn.put(String.format("list%d-hit", idx), esRes.getHitList());
			rtn.put(String.format("list%d-hl", idx), esRes.getHlList());
			
			idx++;
		}

		//facetGubun = response.getAggregations().get("techTypeFacet");
		
		Terms facetGubun = null;

		Map<String, Object> facetMap = new HashMap<String, Object>();

		facetGubun = response.getAggregations().get("STDIV");
		if( facetGubun != null ){
			List<ListOrderedMap> facets = new ArrayList<ListOrderedMap>();
			for( Terms.Bucket bucket : facetGubun.getBuckets() ){

				if( StringUtils.isEmpty( (String)bucket.getKey() ) ) continue;

				ListOrderedMap map = new ListOrderedMap();
				map.put("facetGubun", "ipcTypeFacet");		//국제특허코드
				map.put("gubun_code", bucket.getKey());
				map.put("gubun_code_nm", (String)bucket.getKey() );
				map.put("facet_count", bucket.getDocCount());

				facets.add(map);
			}

			facetMap.put("STDIV_FACET", facets);
		}

		//facetGubun = response.getAggregations().get("mtrSourceNmFacet");
		/*if( facetGubun != null ){
			List<EgovMap> facets = new ArrayList<EgovMap>();
			for( Terms.Bucket bucket : facetGubun.getBuckets() ){

				if( StringUtils.isEmpty( (String)bucket.getKey() ) ) continue;

				EgovMap map = new EgovMap();
				map.put("facetGubun", "mtrSourceNmFacet");		//제공기관
				map.put("gubun_code", bucket.getKey());
				map.put("gubun_code_nm", (String)bucket.getKey() );
				map.put("facet_count", bucket.getDocCount());

				facets.add(map);
			}
			facetMap.put("mtrSourceNmFacet", facets);
		}*/

		//facetGubun = response.getAggregations().get("techPftCdFacet");
		/*if( facetGubun != null ){
			List<EgovMap> facets = new ArrayList<EgovMap>();
			for( Terms.Bucket bucket : facetGubun.getBuckets() ){

				if( StringUtils.isEmpty( (String)bucket.getKey() ) ) continue;

				EgovMap map = new EgovMap();

				map.put("facetGubun", "techPftCdFacet");		//소장자료 검색용
				map.put("gubun_code", bucket.getKey());
				map.put("gubun_code_nm", (String)bucket.getKey() );
				map.put("facet_count", bucket.getDocCount());

				facets.add(map);
			}
			facetMap.put("techPftCdFacet", facets);
		}*/


		
		rtn.put("facetMap", facetMap);
		rtn.put("totalHits", hits.totalHits);

		return rtn;

	}
	
}// controller 

