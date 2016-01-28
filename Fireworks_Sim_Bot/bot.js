console.log('The image bot is starting');

var Twit = require('twit');

var T = new Twit({
    consumer_key:         'place code here'
  , consumer_secret:      'place code here'
  , access_token:         'place code here'
  , access_token_secret:  'place code here'
})

var exec = require('child_process').exec;
var fs = require('fs');

tweetIt();
setInterval(tweetIt, 1000*60*60)

function tweetIt(){
	var cmd = 'processing-java --sketch=twiter_bot_gird/ --force --output=export/ --run';
	exec(cmd, processing);
	
	function processing(){
		console.log('finished');
		var fileName = 'twiter_bot_gird/export.gif'
		var params = {
			encoding: 'base64'
		}
		var b64 = fs.readFileSync(fileName, params);
		T.post('media/upload', {media_data: b64},uploaded);
	
		function uploaded(err, data, response){
			var id = data.media_id_string;
			var tweet = {
				status: '',
				media_ids: [id]
			}
			T.post('statuses/update', tweet, tweeted);
		}
		function tweeted(err, data, response){
			console.log("It worked!");
		}
	}
}