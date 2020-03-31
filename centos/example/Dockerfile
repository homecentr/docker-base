FROM base-centos:local as base

FROM centos:8

COPY --from=base / /

# RUN yum install -y shadow

ENTRYPOINT [ "/init" ]