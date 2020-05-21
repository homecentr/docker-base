FROM alpine:3.11.6

LABEL maintainer="Lukas Holota <me@lholota.com>"

ENV S6_VERSION="v1.22.1.0"
ENV CPU_ARCH="amd64"

ENV PUID=7077
ENV PGID=7077
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

ADD https://github.com/just-containers/s6-overlay/releases/download/${S6_VERSION}/s6-overlay-${CPU_ARCH}.tar.gz /tmp/

RUN tar xzf /tmp/s6-overlay-${CPU_ARCH}.tar.gz -C / && \
    apk add --no-cache shadow=4.7-r1 && \
    rm /tmp/s6-overlay-${CPU_ARCH}.tar.gz

COPY ./fs /

RUN chmod a+x /usr/sbin/runas

ENTRYPOINT [ "/init" ]