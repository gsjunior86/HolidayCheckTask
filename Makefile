default: build


build:
	sbt reload clean compile package

dockerize:
	docker build -f Dockerfile.base -t gsjunior/holidaycheck_challenge .
