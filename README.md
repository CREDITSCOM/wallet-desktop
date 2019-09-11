<img src="https://raw.githubusercontent.com/CREDITSCOM/Documentation/master/Src/Logo_Credits_horizontal_black.png" align="center">

[Documentation](https://developers.credits.com/en/Articles/Platform) \|
[Guides](https://developers.credits.com/en/Articles/Guides) \|
[News](https://credits.com/en/Home/News)

[![Twitter](https://img.shields.io/twitter/follow/creditscom.svg?label=Follow&style=social)](https://twitter.com/intent/follow?screen_name=creditscom)
[![AGPL License](https://img.shields.io/github/license/CREDITSCOM/ewa.svg?color=green&style=plastic)](contract-executor/LICENSE)
[![Build Status](http://89.111.33.166:8080/buildStatus/icon?job=ewa&lastBuild)](http://89.111.33.166:8080/job/ewa//lastBuild/)

<h1>CS Desktop Wallet</h3>
<h2>What's CS Desktop wallet?</h2>
<p>CS Desktop Wallet - the client application is a desktop version of the wallet. Wallet Desktop is implemented in Java. This version of the wallet provides a high level of security, privacy and stability.
Wallet Desktop only works in conjunction with the network node running on the user's computer and includes the Smart Contract executor module.
Current functionality:</p>
<ul>
<li>View the balance</li>
<li>View transaction history</li>
<li>View transactions with smart contracts</li>
<li>Create a transaction</li>
<li>Creating a new token, using a smart contract</li>
<li>Create and download a smart contract</li>
<li>Operations with smart contracts</li>
</ul>
<h2>System requirements:</h2>
<p>Minimum system requirements:

Operating system: Windows® 7 / Windows® 8 / Windows® 10 64-bit (with the last update package)
Processor (CPU): with frequency of 1 GHz (or faster) with PAE, NX and SSE2 support;
Memory (RAM): 2–4 Gb
HDD: 1 Gb
Internet connection: 3 Mbit/s</p>

<p>Recommended system requirements:

Operating system: Windows® 7 / Windows® 8 / Windows® 10 64-bit (with the last update package)
Processor (CPU): Intel® Core ™ i3 or AMD Phenom ™ X3 8650
Memory (RAM): 4–8 Gb
SSD: 1 Gb
Internet connection: 5 Mbit/s.</p>
##### How to build
**Maven build compilation from sources files for**

**wallet-desktop**

**Maven **- Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information.This tool can be used for building and managing any Java-based project by making the build process easy.

The major prerequisite needed in order to use Maven is Java SDK installation because the tool is written in Java and primarily it is used to build Java programs.

The installation process of Maven basically implies extracting the archive and adding the bin folder with the mvn command to the PATH.

Below the detailed steps that are needed for compiling wallet-desktop from sources files:



1. First at all, you must download and install JDK and Add ‘JAVA_HOME’ Environment Variable. After installing Java you must add JAVA_HOME variable to JDK install folder. In addition, ‘java/bin’ directory must be included in ‘PATH’ variable.

<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/1.PNG" align="center">

2. Download and install Maven and set the ‘M2_HOME’ and ‘MAVEN_HOME’ variables to maven installation folder. You can download Maven [here](https://maven.apache.org/download.cgi).
    
<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/2.png" align="center">


3. Afterward, ‘maven/bin’ directory must be included in ‘PATH’ variable also. To run maven from command prompt, this is necessary so update the PATH variable with 'maven/bin' directory. 

    
<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/3.png" align="center">


4. Check the installation by typing the following command in the console:
```mvn -version```

 <img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/4.PNG" align="center">   


5. Install [Git](https://git-scm.com/download/win) and clone the thrift-api-client repository from GitHub: [https://github.com/CREDITSCOM/thrift-api-client](https://github.com/CREDITSCOM/thrift-api-client)
6. Change to the directory by using command:```cd```
7. Once on the directory write the following command in the prompt to build compilation from sources files: 
```mvn clean install```

<img src="/.github/readme-images/4.PNG" align="center"> 

8. Now, repeat the process for smart-contract-api repository. Clone it from the repository: [https://github.com/CREDITSCOM/smart-contract-api](https://github.com/CREDITSCOM/smart-contract-api)
9. Change to the directory by using command: ```cd``` Then build compilation by using the command: 
```mvn clean install```

    
<img src="/.github/readme-images/5.png" align="center"> 

10. Finally, clone the wallet-desktop repository from: [https://github.com/CREDITSCOM/wallet-desktop](https://github.com/CREDITSCOM/wallet-desktop)


<img src="/.github/readme-images/6.PNG" align="center"> 


11. Change to the wallet-desktop directory by using command ```cd``` and build it: 
```mvn clean install```

    
<img src="/.github/readme-images/7.PNG" align="center"> 

12. If the compilation was performed properly, you should get the following result:

    
<img src="/.github/readme-images/8.PNG" align="center"> 

13. For run wallet you have to install openjfx sdk and then you need specify path to lib folder of openjfx. 
Use following command as example
 "%your_module_path_here%" - path to the lib folder OpenJFX SDK
```shell
java --module-path %your_module_path_here% --add-modules=javafx.controls,javafx.fxml,javafx.graphics -jar wallet-desktop.jar`
```

<!-- Docs to Markdown version 1.0β17 -->

<h2>Contribution</h2>
<p>Thank you for considering to help out with the source code! We welcome contributions from anyone on the internet, and are grateful for even the smallest of fixes!
If you'd like to contribute to CS-Wallet-Desktop, please fork, fix, commit and send a pull request for the maintainers to review and merge into the main code base. If you wish to submit more complex changes though, please check up with the core devs first on our <a href="https://developers.credits.com/">Developers portal</a> and <a href="https://github.com/CREDITSCOM/Documentation/blob/master/Contribution.md"> Contribution file</a> to ensure those changes are in line with the general philosophy of the project and/or get some early feedback which can make both your efforts much lighter as well as our review and merge procedures quick and simple.
Please make sure your contributions adhere to our coding guidelines:</p>
<ul>
<li>Code must adhere to the <a href="https://google.github.io/styleguide/javaguide.html">Google Java Style Guide</a></li>
<li>Code must be well documented adhering the Google’s guidelines</li>
<li>Pull requests need to be based on and opened against the master branch</li>
<li>Commit messages should be prefixed with the package(s) they modify</li>
</ul>
<h3>Resources</h3>

<a href="https://credits.com//">CREDITS Website</a>

<a href="https://github.com/CREDITSCOM/DOCUMENTATION">Documentation</a>

<a href="https://credits.com/Content/Docs/TechnicalWhitePaperCREDITSEng.pdf">Whitepaper</a>

<a href="https://credits.com/Content/Docs/TechnicalPaperENG.pdf">Technical paper</a>

<a href="https://developers.credits.com/">Developers portal</a>

<a href="http://forum.credits.com/">Credits forum</a>
<h3>Community links</h3>
   <a href="https://t.me/creditscom"><img src ="https://simpleicons.org/icons/telegram.svg" height=40 widht=40 ></a>
   <a href="https://twitter.com/creditscom"><img src ="https://simpleicons.org/icons/twitter.svg" height=40 widht=40 ></a>
   <a href="https://www.reddit.com/r/CreditsOfficial/"><img src ="https://simpleicons.org/icons/reddit.svg" height=40 widht=40></a> 
   <a href="https://medium.com/@credits"><img src="https://simpleicons.org/icons/medium.svg" height=40 widht=40></a>
   <a href="https://www.instagram.com/credits_com/"><img src="https://simpleicons.org/icons/facebook.svg" height=40 widht=40></a>
   <a href="https://www.facebook.com/creditscom"><img src="https://simpleicons.org/icons/instagram.svg" height=40 widht=40></a>
   <a href="https://www.youtube.com/channel/UC7kjX_jgauCqmf_a4fqLGOQ"><img src="https://simpleicons.org/icons/youtube.svg" height=40 widht=40></a>
