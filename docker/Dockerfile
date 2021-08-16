FROM  registry.access.redhat.com/ubi8/openjdk-11

ARG DISTRIB_DIRECTORY

COPY  ${DISTRIB_DIRECTORY} /usr/app/

EXPOSE 8081

ENTRYPOINT [ "sh", "-c", "exec java -jar /usr/app/cache-db-loader.jar" ]
