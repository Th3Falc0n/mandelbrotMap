FROM lolhens/sbt-graal:22.0.0.2.2-java11 as builder

COPY . .
ARG CI_VERSION
RUN sbt assembly
RUN cp "$(find target/scala-* -type f -name '*.sh.bat')" /tmp/app

FROM openjdk:19

COPY --from=builder /tmp/app /opt/app

CMD exec /opt/app
