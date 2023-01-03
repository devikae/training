var express = require('express')
var app = express()
var bodyParser = require('body-parser')

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended:true}))
app.set('view engine', 'ejs')

app.listen(3000, function() {
  console.log('mission server on!')
})

app.get('/', function(req,res){
  res.render('main.ejs', {})
})

app.post('/search', function(req,res){
  console.log('#### request.body : ', req.body)
  var resData= {'result': 'OK'}
  
  for(let i = 0; i<20; i++){
    if(req.body[i] === undefined){
      console.log('마지막 = ' + req.body[i]);
      break;
    }

    resData[i] = req.body[i];
    console.log('push data= ', resData[i])

  }

  res.json(resData)
})