ROOT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )/../.." &> /dev/null && pwd )

docker run -it --rm -d -p 8080:80 --name webN -v "${ROOT_DIR}/target/site":/usr/share/nginx/html -v "${ROOT_DIR}/tools/nginx/conf.d":/etc/nginx/conf.d nginx