# 概要
The Movie Database(https://www.themoviedb.org) から映画情報を取得し、表示するアプリです。

映画情報は
* 人気順
* 評価順
* ブックマークのみ

を表示することが可能です。

各映画につき、
* タイトル、公開日、平均投票点、概要
* YouTubeの予告編(複数)
* 批評一覧

を表示できます。

# デモ
各映画のポスターを眺めた後、ゴジラの映画をお気に入りに追加し、YouTubeの予告編を見るまでのデモです。
![screencast.gif](screencast.gif)

# How to run this app
1. Create an account at https://www.themoviedb.org
2. Find your API key at https://www.themoviedb.org/settings/api
3. Append the following to `gradle.properties`
```
TheMovieDatabaseApiKey="Your API key comes here"
```
