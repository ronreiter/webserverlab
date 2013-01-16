del /Q /F /S bin
del /Q /F /S out
md bin
md bin\lab
cd .\src\lab\
javac -classpath "..\..\lib\json_simple-1.1.jar" -d ..\..\bin Main.java Logger.java ConnectionHandler.java Crawler.java CrawlerRequestHandler.java Resource.java ResourceQueue.java ResultFileGenerator.java RobotsParser.java CrawlerRequestQueue.java CrawlerRequestsJSONHandler.java CrawlRequest.java CrawlTask.java CrawlTaskPool.java Downloader.java Analyzer.java Mutex.java ReportFileHandler.java ConfigManager.java ConnectionPool.java FileRequestHandler.java HttpRequest.java HttpResponse.java RequestHandler.java RequestRouter.java WebServer.java
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