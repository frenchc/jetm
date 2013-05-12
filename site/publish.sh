#!/bin/sh

EXPORT_USER=root
EXPORT_HOST=void.fm
EXPORT_PATH=/www/jens/jetm/htdocs

rsync --exclude '*.sh' --delete --delete-excluded --recursive -v -a -u -e ssh * ${EXPORT_USER}@${EXPORT_HOST}:${EXPORT_PATH}