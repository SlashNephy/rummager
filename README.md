# rummager

🔍 Let's find tweets that mention you on Twitter. (ego-searching)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.6-blue)](https://kotlinlang.org)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SlashNephy/rummager)](https://github.com/SlashNephy/rummager/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SlashNephy/rummager/Docker)](https://hub.docker.com/r/slashnephy/rummager)
[![Docker Image Size (tag)](https://img.shields.io/docker/image-size/slashnephy/rummager/latest)](https://hub.docker.com/r/slashnephy/rummager)
[![Docker Pulls](https://img.shields.io/docker/pulls/slashnephy/rummager)](https://hub.docker.com/r/slashnephy/rummager)
[![license](https://img.shields.io/github/license/SlashNephy/rummager)](https://github.com/SlashNephy/rummager/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/SlashNephy/rummager)](https://github.com/SlashNephy/rummager/issues)
[![pull requests](https://img.shields.io/github/issues-pr/SlashNephy/rummager)](https://github.com/SlashNephy/rummager/pulls)

# これはなに

Twitter のエゴサ結果を Discord Webhook で通知するツールです。

[![screenshot.png](https://raw.githubusercontent.com/SlashNephy/rummager/master/docs/screenshot.png)](https://github.com/SlashNephy/rummager)

## Requirements

- Java 17 or later

## Get Started

### Docker

There are some image tags.

- `slashnephy/rummager:latest`  
  Automatically published every push to `master` branch.
- `slashnephy/rummager:dev`  
  Automatically published every push to `dev` branch.
- `slashnephy/rummager:<version>`  
  Coresponding to release tags on GitHub.

`docker-compose.yml`

```yaml
version: '3.8'

services:
  rummager:
    container_name: rummager
    image: slashnephy/rummager:latest
    restart: always
    environment:
      # Twitter の資格情報 (必須)
      TWITTER_CK: xxx
      TWITTER_CS: xxx
      TWITTER_AT: xxx
      TWITTER_ATS: xxx
      # Twitter のプライベート API を使用するかどうか
      # CK, CS に公式キーが必要です
      # 検索結果のリミットがなくなります
      USE_PRIVATE_API: 'false'

      # Twitter の検索クエリ (必須)
      SEARCH_QUERY: hoge OR foo OR bar
      # 検索する間隔 (秒)
      SEARCH_INTERVAL_SECONDS: 30
      # 無視するツイート元 (複数指定可能)
      IGNORE_SOURCES: twittbot.net,今日のツイライフ
      # 無視するユーザのスクリーンネーム (複数指定可能)
      IGNORE_SCREEN_NAMES: TwitterJP
      # Discord Webhook の通知先 (必須)
      DISCORD_WEBHOOK_URL: https://xxx
      # ツイートの投稿者がフォロワーだったときの Discord Webhook の通知先
      DISCORD_WEBHOOK_URL_FROM_FOLLOWERS: https://xxx
```
