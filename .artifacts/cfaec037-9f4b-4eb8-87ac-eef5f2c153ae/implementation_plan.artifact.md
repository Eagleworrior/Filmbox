# Implementation Plan - NewsData.io News Tab Integration & Endless Infinite Scroll

Integrate the NewsData.io API (`pub_fe027c61954549009dade8e36feb77f7`) into Film Box as a new **"News"** tab (`📰 News`) with categorized articles, article images, headlines, summaries, endless infinite scrolling (latest at top), and "Read More" links directing users to the official FilmBox Blogger site (`https://filmboxproductions.blogspot.com`).

## User Review Required

> [!IMPORTANT]
> - **NewsData.io API Integration**: Uses API key `pub_fe027c61954549009dade8e36feb77f7` to fetch real-time categorized news (Entertainment, Technology, Sports, World).
> - **New Navigation Tab**: Added `<button data-v="news">📰 News</button>` to the top header navbar.
> - **Rich Article Layout**: Displays article images, category badges, bold headlines, and content previews, sorted with the latest news at the top.
> - **Endless Infinite Scroll**: Automatically loads more news articles as the user scrolls down.
> - **"Read More" Blogger Redirect**: Directs users to `https://filmboxproductions.blogspot.com` for full articles.

## Proposed Changes

### Web Application & News Dashboard
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)`
- **Add News Tab**: Add News button in `<nav id="nav">` and `news: newsView` in `render()`.
- **Fetch NewsData.io**: Implement `newsView()` and `fetchNews(append)` querying `https://newsdata.io/api/1/news?apikey=pub_fe027c61954549009dade8e36feb77f7&category=entertainment,technology,world,sports&language=en`.
- **Endless Scroll**: Integrate news pagination with `window.onscroll`.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Tap the **News** tab to verify categorized news articles load with images and headlines.
- Test endless scroll by scrolling down to load older news.
- Tap "Read More" to confirm redirection to the official Blogger site.
