FROM php:8.2-apache
COPY --from=composer /usr/bin/composer /usr/bin/composer
RUN apt-get update && apt-get -y install libzip-dev libicu-dev
RUN a2enmod rewrite
RUN docker-php-ext-install pdo pdo_mysql
RUN pecl install xdebug-3.2.0
RUN docker-php-ext-enable xdebug
RUN echo "xdebug.mode=debug" >> /usr/local/etc/php/php.ini
RUN echo "xdebug.client_host=host.docker.internal" >> /usr/local/etc/php/php.ini
WORKDIR /var/www/html
