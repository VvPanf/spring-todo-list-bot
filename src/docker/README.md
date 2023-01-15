# Instruction 
1) Drop the jar file with bot into this folder
2) Start docker-compose using command: 
 <code> sudo docker-compose --env-file .env up -d --build </code>
3) Register bot in telegram
 <code> curl -F "url=https://your-site.ru" -F "certificate=@cert.pem" https://api.telegram.org/botYOUR-TOKEN/setWebhook </code>