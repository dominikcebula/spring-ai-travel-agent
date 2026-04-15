FROM nginx:1.29

COPY nginx.conf /etc/nginx/nginx.conf

COPY build/ /usr/share/nginx/html/

EXPOSE 8080

ENTRYPOINT ["nginx", "-c", "/etc/nginx/nginx.conf"]
CMD ["-g", "daemon off;"]
