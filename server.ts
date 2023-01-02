// 설치한 모듈 불러오기
import express from "express";
import morgan from "morgan";
import { AppDataSource } from "./data-source";

// express는 최상위 함수 app에 담아 상수로 만들어 준다.
const app = express();

// 미들웨어 추가 
app.use(express.json());
// req에서 json 형식 파일을 보내올 때 해석해 사용하려면 express.json이 필요하다.
app.use(morgan('dev'));
// dev 말고도 여러 mode가 있지만 개발 환경에선 보통 dev 옵션을 이용 한다.


// '/'으로 get 요청이 온다면 running 표시 
app.get('/', (_,res) => res.send('running'));

let port = 4000; // back end port 설정

// app.listen으로 아까 express 실행 
app.listen(port, async () => {
  console.log(`server running at http://localhost:${port}`);
  
  // db connection

  AppDataSource.initialize().then( () => {

    console.log('db initialized')
  }).catch(error => console.log('db error'))

});