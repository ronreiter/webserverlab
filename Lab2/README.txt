Spiderman Crawler
=================

Tamir Berliner and Ron Reiter

Introduction
============

The Spiderman crawler crawls website and gathers statistics on the crawled domain. Statistics include number of images,
videos, documents and pages (and their sizes), number of links, and the list of domains connected to the crawled
domain.

How to use
==========

To use, please run compile.bat and then run.bat. Then, open your webpage at http://localhost:8080 and start crawling!
To start a new job, please enter a URL of a domain you would like to start crawling from under the "Domain to crawl"
text box. Then, select "Ignore robots.txt" if you'd like the crawler to disregard the instructions there.

Design
======

The crawler is made out of two main sections - a user interface back-end / front-end and the crawler itself.

User interface
--------------

The crawler's interface is a web-based interface, which uses a browser to interact with the user. When the crawler
is active, it serves requests on port 80 using the WebServer module, which passes HttpRequests to threads
through a queue. The threads then use the RequestRouter and the relevant class according to the path and domain
to handle the request, and return an HttpResponse to the user. The CrawlerRequestHandler handles the main
user interaction of the application - which includes the statistics window on the currently running crawl requests,
and the crawl request addition form. The completed job reports page shows the list of crawl requests that have been
completed (and also stores them under the root/reports directory). When viewing the reports, one must access them
only through the completed reports page or through links connecting between the reports themselves.

The main web interface uses AJAX to update the current status of the crawler. Once jobs are completed, their status
changes from "Working" to "Finished". On top of the form, the general status of the crawler is shown - how many
crawler threads are currently working, and if we can add another task. It is technically possible to enqueue more
tasks then workers, but this feature has been disabled intentionally. The UI also shows the last success or error
of adding a crawl request, and the reason for failure, if there was one.

The front end uses jQuery to fetch the data from the CrawlerRequestsJSONHandler, an endpoint which returns a JSON list
of objects that represent crawl requests. It then uses a client templating library (called Handlebars.js) to build
the display widgets dynamically.

The completed jobs page simply iterates over the files in the reports directory, and links to them so the user
can click on the file it wants to view. Viewing the file is done using a special FileRequestHandler called the
ReportFileHandler, which makes sure that the user came from the allowed referrers.

Crawler
-------

Once a task is added, the Crawler manager adds a new CrawlRequest to its collection, and queues it in the
CrawlerRequestQueue. The CrawlTaskPool manages a list of CrawlTask threads, which attempt to receive a CrawlRequest
from the CrawlerRequestQueue. Once the CrawlTask starts working on the request, it updates its information so the
user interface can receive the data at any time.

The CrawlTask holds a pool of analyzer threads and downloader threads, which move Resource objects between them using
two queues - the toAnalyze queue and toDownload queue, queues which hold Resource objects that need to be downloaded
and then analyzed. Each link that is analyzed and leads to a resource gets counted and the statistics are saved in
the CrawlerRequest data model. A link that leads to another HTML page is parsed, and the image and anchor links are
then extracted and inserted as new Resource objects to the download queue.

The crawling starts by initializing a RobotsParser and fetching the robots.txt file, if specified. Each URL which is
processed will only be processed if it conforms to the robots.txt file, by asking the RobotsParser if the URL is
allowed to download.

The crawling ends when both download and analyze queues are empty, and all worker threads (workers and analyzers)
are finished working on their current task.

The CrawlTask waits for this event, and then uses the ResultFileGenerator to generate the HTML file to the reports
page using the CrawlRequest data object.






