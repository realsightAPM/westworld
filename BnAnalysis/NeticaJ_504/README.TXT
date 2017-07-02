
	   
	                   Netica-J for 32-bit and 64-bit Windows
	                   ======================================
			              Version:  5.04
		                 Release Date:  Jan 21, 2012


LICENSE AGREEMENT
-----------------

Before using Netica, make sure you accept the license agreement,
which is included with this package.  If not, return Netica unused for a refund.


MANUAL and ONSCREEN HELP
------------------------

After unzipping this zip archive, please open the Netica-J manual in:
\docs\NeticaJ_Man.pdf
and read the installation instructions it contains.

Also, be sure to open  docs/javadocs/index.html  with your web browser,
and bookmark that page, since it is a great reference, and is the most 
useful tool for working with Netica-J.

The same documentation for the latest version of Netica-J can be found online at:
http://www.norsys.com/netica-j/docs/javadocs/index.html


LICENSE PASSWORD
----------------

If you have been issued a license password, you pass it as a string to
the Environ constructor to fully enable Netica.  This is described more fully
in the manual.


BUILD DETAILS
-------------
32bit version built using SUN JDK 1.6.0_29
64bit version built using SUN JDK 1.6.0_30


64/32 BIT 
---------

This distribution contains both the 32bit (default) and 64bit libraries.
To use the 64bit libraries, move them into the directories containing the 32bit libraries, 
replacing the 32bit libraries. 


PROGRAMMING LANGUAGES
---------------------

Netica API is available for programming in Java, C#, Visual Basic, C and C++.
For more information on Netica API for different languages, see the Norsys web site.


FUTURE VERSIONS
---------------

If you just purchased Netica API, your purchase price includes all version 
5.xx releases of Netica API, so your license password will work on all of them.  
They may be downloaded from the Norsys web site.


PROBLEMS
--------

If you have problems, contact Norsys at:    netica-j@norsys.com

Norsys Software Corp.
www.norsys.com

==================================================================================


CHANGES
-------

For much more details, see docs/Netica-J ChangeLog.txt


Version 5 changes (from 4.18 to 5.04)
=====================================

- Many major speed improvments, especially when operating in a multi-threaded environment.

- Can enter "action values" to nodes, which follow semantics of Pearl's do-calculus of 
  causal intervention.  These are accessable via Node.action(), 
  which works similar to Node.finding().

- Major improvements to time-series net and dynamic bayes net (DBN) capability.  
  Now does a "burn-in" to generate initial-state probability distribution.
  Improved or added functions Net.expandTimeSeries, Node.setNodeInputDelay, 
  Node.setNodePersistance, Net.getNodeAtTime.

- Major improvements to Net.generateRandomCase (including using some determinstic 
  forward propagation for continuous real values).

- Expanded on the 'options' parameter for Net.copyNodes and the Net copy constructor, 
  which can control what gets copied.

- Added Environ.controlConcurrancy, and made all of Netica able to concurrently run 
  Bayes nets in separate threads if desired.

- Improved Netica's architecture for pseudo-random operations, and made it possible 
  to act deterministically under multi-threading.
  Added the RandomGenerator class and Net.setRandomGenerator.
  
- EM learning now handles multiple CPT tables constrained to be the same 
  (indicated by the node user field "CPT_ID" having the same value).
  Also it now leaves any findings that were originally in the net while learning.


Version 4 changes (from 3.25 to 4.18)
=====================================

- Added Net.createCustomReport function to generate user-customizable reports on a 
  wide variety of things including: network, nodes, states, cases, findings, 
  inference results, sensitivity, internal compiled structure.

- Added function Node.enterCalibration to work with calibration findings like Netica Application.

- Can now do probabilistic inference when there are disconnected links.

- Net copy constructor now allows for "no_nodes" and "no_links" in its 'options' string, 
  and Net.copyNodes now allows for "no_links" and "no_tables" in its 'options' string.

- Now API can compile and infer okay if nodes have some or all CPT entries or tables missing, 
  the same way as Netica Application handles it.

- Equation Improvements:
    BetaDist and FDist functions take a wider range of arguments
    NoisyOrFaultyDist function added

- Node.getStateFuncTable has other negative return values besides UNDEF_STATE.

- minor bug fixes


Version 3 changes (from 2.21 to 3.25)
=====================================

- new Learner class added, which offers batch learning: EM learning and 
  gradient-descent learning.
- added a new DatabaseManager class for loading cases from, and
  writing findings to, any ODBC-compliant database (ORACLE, Sybase,
  MySQL, SQL Server, Access, even text files).  Supports complete
  SQL query language to retrieve only the cases you want.
- added /gui package for displaying Bayesian Nets in Java.  See the examples/
  directory for two sample programs that use the new gui package.
- added ability to add or delete states to a discrete node.
- added ability to reorder the states of a discrete node.
- added method Streamer.setPassword(), allowing you to encrypt/decrypt files.
- supports new binary file format (.neta), greatly reducing the size of
  large net files saved to disk.
- created a new javadocs-like web help system that includes a complete
  topic index.  In your web browser, click on docs/javadocs/index.html
  to lauch this new help system.
- renamed abstract class Generic to General, to avoid potential confusion
  with java 1.4 generics.
- added Value class for managing findings.  Use Node.finding() to access the
  finding Value object for a node.
- added CaseSet class for managing cases.
- unicode support
- new option for fast setting and reading of probability tables.

