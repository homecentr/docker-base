FROM alpine:3.11.6 as s6

ENV S6_VERSION="v1.22.1.0"
ENV CPU_ARCH="amd64"

ADD https://github.com/just-containers/s6-overlay/releases/download/${S6_VERSION}/s6-overlay-${CPU_ARCH}.tar.gz /tmp/

RUN mkdir /s6 && \
    tar xzf /tmp/s6-overlay-${CPU_ARCH}.tar.gz -C /s6

FROM scratch

COPY --from=s6 /s6 /

COPY ./fs /