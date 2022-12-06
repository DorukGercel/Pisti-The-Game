FROM maven:3.8.6-openjdk-18
WORKDIR /home/pisti-server
COPY . /home/pisti-server
RUN mvn install -Dmaven.test.skip=true
ENTRYPOINT mvn spring-boot:run