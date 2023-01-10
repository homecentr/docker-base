FROM alpine:3.17.1

LABEL maintainer="Lukas Holota <me@lholota.com>"

ENV S6_VERSION="v1.22.1.0"
ENV CPU_ARCH="amd64"
ENV PUID=7077
ENV PGID=7077
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

ADD https://github.com/just-containers/s6-overlay/releases/download/${S6_VERSION}/s6-overlay-${CPU_ARCH}.tar.gz /tmp/

RUN tar xzf /tmp/s6-overlay-${CPU_ARCH}.tar.gz -C / && \
    # These packages are included in the base image. They are explicitly upgrade to vulnerabilities below
    apk add --no-cache \
        # CVE-2021-36159
        apk-tools=2.12.9-r3 \
        # CVE-2021-3711, CVE-2021-3712
        libssl1.1=1.1.1o-r0 \
        shadow=4.10-r3 && \
    rm /tmp/s6-overlay-${CPU_ARCH}.tar.gz

COPY ./fs /

RUN chmod a+x /usr/sbin/runas

ENTRYPOINT [ "/init" ]