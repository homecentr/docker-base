FROM base as base

FROM alpine

COPY --from=base / /

RUN apk add --no-cache shadow

ENTRYPOINT [ "/init" ]