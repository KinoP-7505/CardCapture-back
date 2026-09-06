# ---------------------------------------------------
# 1. ビルドステージ (Java 21 JDK)
# ---------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Gradleの設定ファイル類を先にコピー（キャッシュ効率化）
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 実行権限の付与
RUN chmod +x gradlew

# 依存関係のキャッシュ取得（ビルド速度向上）
RUN ./gradlew dependencies --no-daemon

# ソースコードをコピーしてJARをビルド (テストはスキップ)
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# ---------------------------------------------------
# 2. 実行ステージ (Java 21 JRE)
# ---------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ビルドステージから生成されたJARファイルのみをコピー
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Renderのポート割り当て用
EXPOSE 8080

# アプリの起動
ENTRYPOINT ["java", "-jar", "app.jar"]