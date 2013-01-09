md bin
cd .\src\lab\
javac -d ..\..\bin Main.java Logger.java Crawler.java CrawlerRequestHandler.java Resource.java ResourceQueue.java ResultFileGenerator.java RobotsParser.java CrawlerRequestQueue.java CrawlerRequestsJSONHandler.java CrawlRequest.java CrawlTask.java CrawlTaskPool.java Downloader.java Analyzer.java CompletedJobsRequestHandler.java Mutex.java ReportFileHandler.java ConfigManager.java ConnectionPool.java FileRequestHandler.java HttpRequest.java HttpResponse.java RequestHandler.java RequestRouter.java WebServer.java
copy ..\..\bin\lab\main.class ..\..\bin\main.class
cd ..\..\
echo off
echo
echo ===============================================================================
echo To Run copy the following line:
echo java -classpath .\bin\ lab.Main
echo -- OR --
echo just type "run"
echo ===============================================================================