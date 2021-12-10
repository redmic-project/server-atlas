ARG PARENT_IMAGE_NAME=registry.gitlab.com/redmic-project/docker/redmic-server
ARG PARENT_IMAGE_TAG=latest

FROM ${PARENT_IMAGE_NAME}:${PARENT_IMAGE_TAG}

COPY /*/dist/*.jar ./

ARG PORT=8080

EXPOSE ${PORT}

ENV PORT=${PORT}
