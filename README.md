cicstart
========

CICSTART: Common Interfaces for Cloud Storage, Access, and Resource Utilization (CANARIE RPI SC-04)

During the NEP-61 project, a suite of generalized components and framework was prototyped. These components address several problems identified during the NEP-21 project, and when compiled together into a platform, make up leading-edge middleware to support research. This project aims to enhance the platform, making it more suitable and scaling it up for use by the broader scientific community.

The components that make up this research middleware platform are:

1. A Catalogue Service used to identify and locate data resources across network boundaries. This service is a stand-alone component that can be used by end users (via a portal) independent of the platform. This component can also be used to automate the look-up and retrieval of data by any research software running within the platform. At the same time, this service allows any research software to automatically catalogue the output data generated. Essentially, this component establishes a means of cycling data between any piece of software used by researchers.
2. A File Service used to transfer data from remote data stores to the research software or end users. This service isolates the software and end user from dealing with the complexities of low-level details of differing network protocols, authentication, and specific locations of data files in the remote data stores.
3. An Authentication Service used to authenticate users and software so that authorization of services and resources can be enforced.
4. A Virtual File System Service (VFSS) that allows (s)ftp directory-based access using off-the-shelf ftp client software, as well as web service access by research software and/or other users of the platform to data files. This service also enables an innovative mechanism for distributed storage in the platform.
5. A Proxy Service that manages the allocation of cloud resources and supervises the dispatching of research software runs on those resources.
6. A Proxy Framework, which is a "thin wrapper" used to make legacy software accessible via web service calls, and which allows for interaction with other platform services (Catalogue, File Service, Authentication, VFSS, and Proxy).

The CICSTART platform is intentionally made up of stand-alone components for a number of strategic reasons:

1. Independent of each other, the components can add value to any new project requiring cataloguing and data location services, file transfer and caching, and authentication services.
2. The VFSS is designed to run outside of the cloud. Researchers that already have access to storage outside the cloud can integrate their existing storage into the platform by installing the VFSS. The remote VFSS is accessible by CICSTART and as a result, the research software wrapped in the Proxy framework also has access to the remote VFSS. This innovative approach has a number of unique attributes:
  * The cost of data storage is distributed back to the research community, and allows for horizontal scaling of storage.
  * Provides researchers with the option of keeping their current storage configuration, allowing them to physically store the data within their own network boundaries while still gaining benefit from the rest of the CICSTART platform services.
3. The Proxy service can be installed where the computing resource exists (i.e., on Amazon, on DAIR, or on CESWP) and run independently from the cloud but still has access to the rest of the CICSTART platform services. This innovation allows computations to run closer to the data when necessary, and creates a model for distributed computing.
4. The Proxy framework can be used directly in new development, or used to wrap legacy software such as the Space Weather Modelling Framework, which is accessed via command line only. Once the asset is wrapped, it becomes accessible via standard web interface run through the Proxy service. The researcher also has the option of running the asset directly outside of the CICSTART platform, which allows for easy debugging and testing.

The CICSTART platform reduces the challenges and complexity of providing storage and computing resources to the research.

Please see the wiki for more information.