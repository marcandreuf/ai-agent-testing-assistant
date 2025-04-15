FROM eclipse-temurin:21-jdk-noble

RUN apt-get update && apt-get upgrade -y &&\
    apt-get install -y curl maven openssh-client

# Adding node to sudoers to make locla dev en more flexible for tesing new configurations.
RUN adduser ubuntu sudo
RUN echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# Set the working directory
WORKDIR /home/ubuntu/workspace

USER ubuntu
