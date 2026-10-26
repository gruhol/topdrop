FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:21-jre
# CVE-2026-91403: usuniecie rootow emSign (eMudhra), pod ktorymi wystawiono skompromitowane CA posrednie.
# Dopasowanie po CN, bo nazwy aliasow roznia sie miedzy dystrybucjami JDK. Temurin 21.0.13+ ma juz te rooty usuniete - wtedy krok nic nie robi; mozna go skasowac, gdy tag 21-jre bedzie >= 21.0.13.
RUN keytool -list -v -cacerts -storepass changeit \
      | awk '/^Alias name:/{a=substr($0,13)} /^Owner:.*CN=emSign (ECC Root CA - G3|ECC Root CA - C3|Root CA - C1),/{print a}' \
      | while IFS= read -r a; do keytool -delete -noprompt -cacerts -storepass changeit -alias "$a"; done \
    && ! keytool -list -v -cacerts -storepass changeit | grep -qE '^Owner:.*CN=emSign (ECC Root CA - G3|ECC Root CA - C3|Root CA - C1),'
RUN mkdir -p /app/data
WORKDIR /app
COPY --from=build /build/target/droptop-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -jar \
  -DBASELINKER_TOKEN=$BASELINKER_TOKEN \
  -DDATABASE_IP=$DATABASE_IP \
  -DDATABASE_NAME=$DATABASE_NAME \
  -DDATABASE_PASSWORD=$DATABASE_PASSWORD \
  -DDATABASE_USERNAME=$DATABASE_USERNAME \
  -DPLATON_API_PROD=$PLATON_API_PROD \
  -DPLATON_CHECKORDERSTATUS=$PLATON_CHECKORDERSTATUS \
  -DPLATON_GETINVOICE=$PLATON_GETINVOICE \
  -DPLATON_GETINVOICESLIST=$PLATON_GETINVOICESLIST \
  -DPLATON_GETPUBLICATIONS=$PLATON_GETPUBLICATIONS \
  -DPLATON_GETSTOCKS=$PLATON_GETSTOCKS \
  -DPLATON_ORDER=$PLATON_ORDER \
  -DPLATON_ORDER_DROP=$PLATON_ORDER_DROP \
  -DPLATON_PASSWORD=$PLATON_PASSWORD \
  -DPLATON_USER=$PLATON_USER \
  -DPROFILE=prod /app/app.jar"]