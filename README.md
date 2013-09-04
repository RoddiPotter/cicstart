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
specific licensed software and hardware resources that are not available on the DAIR cloud.

The components that make up the CICSTART platform are:

## Authentication Service
> The Authentication Service used for creating CICSTART accounts and user management (password reset, etc.).  Authorization is required for
> all operations on CICSTART that have end-user data associated with it, as well as some higher level functions like adding a new data server. 
> Note that this component currently does not use HTTPS.  A discussion has been started on the Google group about this. 
> [More details](//github.com/roddipotter/cicstart/wiki/Auth)
### API Documentation
> [User](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/user)

> [Session](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/session)

## Catalogue for File Metadata 
> A Catalogue Service used to identify and locate data resources on the Internet. This service is 
> a stand-alone component that can be used by end users (e.g., via a portal) independent of the platform. This component 
> can also be used to automate the look-up and retrieval of data by any client software running within the platform. 
> At the same time, this service allows any research software to automatically catalogue the output generated data from computational models. 
> This component enables a method for piping data from one computational job to the next. [More details](//github.com/roddipotter/cicstart/wiki/Catalogue)
### API Documentation
> [Project](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/project)

## File Transfer and Cache
> A File Service used to transfer data resources across the Internet to the computational jobs running on cloud resources or directly to end users. 
> This service isolates the researcher from the complexities and details of differing network protocols, 
> authentication, and file locations on the remote data servers.  This component also caches data for improved network efficiency and 
> allows for arbitrary mapping of external keys to the file's MD5 hash.  The arbitrary mapping of keys to file hashes eliminates duplicate file
> data in the cache and provides unlimited vectors for users to organize and recall data files. [More details](//github.com/roddipotter/cicstart/wiki/File)
### API Documentation
> [Cache](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/cache)

> [Host](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/host)

## Virtual File System (VFS)
> A Virtual 'File System Service' that allows end-users and computational jobs to place and get data files from.  The CICSTART services on
> DAIR allow for a limited capacity of VFS space, however the tool can be downloaded and a VFS space can be created on your local environment.
> This system allows for user-control over long term storage of job results, input files, and log data.  The VFS can be access via REST
> interface and has the potential to also offer FTP and SFTP access to the same data (the DAIR installation of CICSTART does not offer FTP or
> SFTP services at this time).  The VFS system interacts with the Auth service for authentication and authorization to VFS resources.
> [More details](//github.com/roddipotter/cicstart/wiki/VFS)  
### API Documentation
> [File System](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/filesystem)

## Macro Service and CICSTART Macro Language
> This service allows the end user to define a set of steps (like a workflow) in the form of a macro using the [CICSTART Macro Language](//github.com/roddipotter/cicstart/wiki/CML) to
> launch VMs (on DAIR).  The macro can interact with other CICSTART services, like getting data from the VFS, the catalogue, and putting data 
> to the VFS.  It can also run arbitrary commands on the system running the macro.  The Macro service can also be used to generate a client 
> that can be downloaded and run locally, still fully interacting with CICSTART services (except for launch VMs).  This can be used for 
> debugging or for cases that do no require a VM, such as accessing software licenses for the local machine only. 
> [More details](//github.com/roddipotter/cicstart/wiki/Macro)
### API Documentation
> [Macro](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json#!/macro)

> [CICSTART Macro Language Documentation](//github.com/roddipotter/cicstart/wiki/CML)

---------------------------------------

##To help you get started:

There are plenty of [examples](//github.com/roddipotter/cicstart/wiki/Examples) for reference.

### For the platform developer
You are extending the CICSTART components for general use or your own interests. The software is built on Java 1.6 and JAXRS.  
The server components need a database to talk with (PostgreSQL is default), but the VFS and Proxy components do not require a database.

1. See [building and deploying the components](//github.com/roddipotter/cicstart/wiki/BuildAndDeploy)
    
### For the platform implementor
You are using the REST APIs offered by CICSTART to build your own data portal.

1. See [REST APIs](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json)
    
### For the model developer
You are developing a computational model and you want it served by CICSTART

1. See [Macro](//github.com/roddipotter/cicstart/wiki/Macro) and [CML](//github.com/roddipotter/cicstart/wiki/CML) documentation
2. See [REST APIs](http://208.75.74.81/cicstart/docs/?input_baseUrl=http://208.75.74.81/cicstart/api/api-docs.json)

---------------------------------------

Open discussion about the platform is conducted through the Google group: 
[CICSTART](https://groups.google.com/forum/?hl=en&fromgroups#!forum/cicstart)

---------------------------------------

All CICSTART components are released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).  This excludes 
computational models developed that run on the CICSTART platform since these models may be released under their own license.  All 
computational models bundled with the CICSTART platform are Apache 2 licensed. See LICENSE for license detail.

