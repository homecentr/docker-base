FROM base-alpine:local as base

FROM alpine:3.11.5

COPY --from=base / /

RUN apk add --no-cache shadow

ENTRYPOINT [ "/init" ]