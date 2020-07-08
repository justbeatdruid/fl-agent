docker:
	mvn clean package
	docker build -t registry.cmcc.com/federatedai/fl-agent:v1.0.0 -f Dockerfile target
