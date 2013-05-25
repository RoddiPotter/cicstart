# Introduction

CICSTART: Common Interfaces for Cloud Storage, Access, and Resource Utilization (CANARIE RPI SC-04)

During the [CANARIE](http://www.canarie.ca/) NEP-61 project developed at the 
[University of Alberta Department of Physics](http://www.physics.ualberta.ca/en/Research/SpacePhysics.aspx) under the direction of 
Dr. Robert Rankin with the space physics group, a framework for cataloguing, accessing, and working with file data from 
various data sources was prototyped. CICSTART is a realization of that prototype, bringing each component to a higher level of 
maturity and building new components required to fulfil the objective "Give a researcher consistent access to data, compute, and 
storage resources by leveraging existing hardware and software assets".  

This object is achieved with the following goals:

1. Registering file urls in a searchable catalogue via a REST api.
2. Accessing files at those urls through a REST api.
3. Launching software to run in pre-defined VMs in an OpenStack cloud.
4. Saving the results generated from running software to long term user storage.
5. Combining these services to simplify or extend the capabilities of software, and exposing them through a REST api.

This framework is currently accessible on the CANARIE [DAIR](http://www.canarie.ca/en/dair-program/about) network, but may also be 
downloaded and configured to run on your infrastructure.  You may wish to download and setup your own platform for access to your 
specific licensed software and hardware resources not available on the DAIR cloud.

The components that make up the CICSTART platform are:

## Catalogue
> A Catalogue Service used to identify and locate data resources on the Internet. This service is 
> a stand-alone component that can be used by end users (e.g., via a portal) independent of the platform. This component 
> can also be used to automate the look-up and retrieval of data by any client software running within the platform. 
> At the same time, this service allows any research software to automatically catalogue the output generated data from computational models. 
> This component enabled a piping mechanism to move data from one computational model job to the next.

## File
> A File Service used to transfer data resources across the Internet to the computational models or end users. This service isolates 
> the software and end user from dealing with the complexities of low-level details of differing network protocols, authentication, 
> and specific file locations of the data files in the remote servers.  This component also caches data for efficiencies and allows for
> arbitrary mapping of external keys to the file's MD5 hash.

## Auth
> An Authentication Service used to authenticate users and software so that authorization of services and resources can be enforced. The
> VFS and other components may use this service to identify the user or request authentication prior to authorizing access to the resource.
> Note that this component currently does not use HTTPS.  A discussion has been started on the Google group about this.

## VFS
> A Virtual 'File System Service' (VFS) that allows (s)ftp directory-based access using off-the-shelf ftp client software, as well as 
> web service access by research software and/or other users of the platform to data files. This service also enables an innovative 
> mechanism for distributed storage in the platform.

## Macro
> A service that is used to generate CML client binary software for running on arbitrary computing resources.  In-progress work to
> start VM instances on behalf of CICSTART users and dispatch CML scripts to those VMs.

## CICSTART Macro Language (CML)
> A scripting language used to interact with CICSTART resources (Catalogue and VFS) and run arbitrary command line processes.  A script
> can be used to find and access scientific data and then run arbitrary software against that data.  The script can also be used to
> define where the results should be placed.  This is an evolving work-in-progress. 

---------------------------------------

The documentation is split into a few categories:

### For the platform developer
You are extending the CICSTART components for general use or your own interests. The software is built on Java 1.6 and JAXRS.  
The server components need a database to talk with (PostgreSQL is default), but the VFS and Proxy components do not require a database.
1. See building and deploying the components
    
### For the platform implementor
You are using the REST APIs offered by CICSTART to build your own data portal.
1. See Components & REST APIs
    
### For the model developer
You are developing a computational model and you want it served by CICSTART
1. See [Macro](//github.com/roddipotter/cicstart/wiki/Macro) and [CML](//github.com/roddipotter/cicstart/wiki/CML) documentation
2. See Components & Rest APIs

---------------------------------------

Open discussion about the platform is conducted through the Google group: 
[CICSTART](https://groups.google.com/forum/?hl=en&fromgroups#!forum/cicstart)

---------------------------------------

All CICSTART components are released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).  This excludes 
computational models developed that run on the CICSTART platform since these models may be released under their own license.  All 
computational models bundled with the CICSTART platform are Apache 2 licensed. See LICENSE for license detail.

