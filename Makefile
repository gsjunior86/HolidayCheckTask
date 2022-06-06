default: dockerize-hadoop



dockerize-hadoop:
	docker build -f hadoop-conf/Dockerfile.hadoop -t gsjunior/hadoop .

