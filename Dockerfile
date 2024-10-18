# Usa una imagen de Gradle para construir el proyecto
FROM gradle:7.6.0-jdk17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de Gradle
COPY build.gradle settings.gradle ./

# Copia los archivos de tu proyecto al contenedor
COPY . .

# Construye el proyecto
RUN gradle build --no-daemon -x test



# Usa una imagen de Amazon Corretto para ejecutar la aplicación
FROM amazoncorretto:17-alpine-jdk

# Copia el archivo JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Establece el punto de entrada de la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]