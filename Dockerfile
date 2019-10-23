ARG PARENT_IMAGE_NAME=registry.gitlab.com/redmic-project/docker/redmic-server
ARG PARENT_IMAGE_TAG=latest

FROM ${PARENT_IMAGE_NAME}:${PARENT_IMAGE_TAG}

COPY /*/dist/*.jar ./

ARG PORT=8080
EXPOSE ${PORT}

HEALTHCHECK \
	--interval=30s \
	--timeout=15s \
	--start-period=3m \
	--retries=10 \
CMD wget --spider -q http://localhost:${PORT}/api/${MICROSERVICE_NAME}/actuator/health
