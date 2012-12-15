
 _          _        _ 
| |    __ _| |__    / |
| |   / _` | '_ \   | |
| |__| (_| | |_) |  | |
|_____\__,_|_.__/   |_|
                       


 _           
| |__  _   _ 
| '_ \| | | |
| |_) | |_| |
|_.__/ \__, |
       |___/ 


 ____               ____      _ _            
|  _ \ ___  _ __   |  _ \ ___(_) |_ ___ _ __ 
| |_) / _ \| '_ \  | |_) / _ \ | __/ _ \ '__|
|  _ < (_) | | | | |  _ <  __/ | ||  __/ |   
|_| \_\___/|_| |_| |_| \_\___|_|\__\___|_|   



                         _ 
          __ _ _ __   __| |
         / _` | '_ \ / _` |
        | (_| | | | | (_| |
         \__,_|_| |_|\__,_|



 _____               _        ____            _ _                 
|_   _|_ _ _ __ ___ (_)_ __  | __ )  ___ _ __| (_)_ __   ___ _ __ 
  | |/ _` | '_ ` _ \| | '__| |  _ \ / _ \ '__| | | '_ \ / _ \ '__|
  | | (_| | | | | | | | |    | |_) |  __/ |  | | | | | |  __/ |   
  |_|\__,_|_| |_| |_|_|_|    |____/ \___|_|  |_|_|_| |_|\___|_|   


The lab web server works in the following way:

The server creates a thread pool that immediately starts to dequeue incoming *TCP* requests from the server queue
The server then listens to incoming connections and enqueues each incoming connection

When a connection is dequeued the dequeuing thread utilizes the httprequest class to parse the incoming data
The parsed requested is then sent to the RequestRouter which calls the correct handler.
	-- we have implemented file handler, params_info handler and a *hopefully bonus* ServerConfigHandler
The relevant handler fills in the HTTPResponse which in turn is being serialized back to the requesting client

* Errors will generate the relevant responses (file not found, internal server error, etc).
* GET and POST parameters are supported.
* The server utilizes an internal logger class with several Log levels. Log levels are echoed as well.
* Logger can be configured to write from a certain debug level, both to the screen and to the log file.
* Log files are created using the current date.
* The administrator has got a password for remote configuration.
* Configuration file is created using the default configuration defined under the configuration manager.
* When setting configuration properties, the configuration file is also saved to the disk.
* Virtual hosts (e.g. www.a.com, www.b.com) are supported (but needs to be enabled in the config.ini file).

