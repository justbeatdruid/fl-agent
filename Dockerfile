FROM registry.cmcc.com/federatedai/python:1.4.0-release

ADD server.conf /data/projects/fate/python/arch/conf/server.conf

RUN yum install -y java-1.8.0-openjdk && yum install -y java-1.8.0-openjdk-devel && \
    echo "export JAVA_HOME=/usr/lib/jvm/java-1.8.0" >> ~/.bashrc && \
    echo "export JRE_HOME=$JAVA_HOME/jre" >> ~/.bashrc && \
    echo "export CLASS_PATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib" >> ~/.bashrc && \
    echo "export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin"

ADD fl-agent-1.0.0.jar /data/projects/fate/

CMD ["java", "-jar", "fl-agent-1.0.0.jar"]
