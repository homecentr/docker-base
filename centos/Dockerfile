FROM alpine:3.11.6 as s6

ENV S6_VERSION="v1.22.1.0"
ENV CPU_ARCH="amd64"

ADD https://github.com/just-containers/s6-overlay/releases/download/${S6_VERSION}/s6-overlay-${CPU_ARCH}.tar.gz /tmp/

    # Create directory for S6
RUN mkdir /s6 && \
    # Extract files from S6
    tar xzf /tmp/s6-overlay-${CPU_ARCH}.tar.gz -C /s6 && \
    # Move files from /bin to /usr/bin (because in CentOS /bin is a symlink to /usr/bin anyway)
    mv /s6/bin/* /s6/usr/bin/ && \
    # Remove the empty directory so it does not overwrite the symlink
    rmdir /s6/bin

FROM centos:8.1.1911

LABEL maintainer="Lukas Holota <me@lholota.com>"

ENV PUID="7077"
ENV PGID="7077"
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

COPY --from=s6 /s6 /

COPY ./fs /

RUN chmod a+x /usr/sbin/runas

ENTRYPOINT [ "/init" ]