FROM alpine:3.14.0

LABEL maintainer="Lukas Holota <me@lholota.com>"

ENV S6_VERSION="v1.22.1.0"
ENV CPU_ARCH="amd64"
ENV PUID=7077
ENV PGID=7077
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

ADD https://github.com/just-containers/s6-overlay/releases/download/${S6_VERSION}/s6-overlay-${CPU_ARCH}.tar.gz /tmp/

RUN tar xzf /tmp/s6-overlay-${CPU_ARCH}.tar.gz -C / && \
    # This package is included in the base image. It's explicitly upgrade to avoid CVE-2021-36159
    apk add --no-cache apk-tools=2.12.7-r0 && \
    apk add --no-cache shadow=4.8.1-r0 && \
    rm /tmp/s6-overlay-${CPU_ARCH}.tar.gz

COPY ./fs /

RUN chmod a+x /usr/sbin/runas

ENTRYPOINT [ "/init" ]