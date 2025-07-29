
FROM openjdk:17
WORKDIR /app
COPY src/ ./src/
RUN javac src/*.java -d .
CMD ["java", "NotesAPI"]
