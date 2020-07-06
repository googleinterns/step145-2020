FROM mcr.microsoft.com/java/maven:8-zulu-debian9

RUN apt-get update && \
  apt-get install -y ca-certificates curl wget build-essential

ENTRYPOINT ["make"]
