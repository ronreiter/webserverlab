cd .\src\lab\
javac -d ..\..\bin Main.java Logger.java ConfigManager.java ConnectionHandler.java ConnectionPool.java FileRequestHandler.java HttpRequest.java HttpResponse.java ParamsInfoRequestHandler.java RequestHandler.java RequestRouter.java ServerConfigRequestHandler.java WebServer.java
copy ..\..\bin\lab\main.class ..\..\bin\main.class
cd ..\..\
echo off
echo
echo ===============================================================================
echo To Run copy the following line:
echo java -classpath .\bin\ lab.Main
echo ===============================================================================