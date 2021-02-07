FROM ubuntu:bionic

WORKDIR /root

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

RUN apt update && apt install -y \
                            liblapack-dev \
                            freeglut3-dev \
                            libxi-dev \
                            libxmu-dev \
                            doxygen \
                            python3 \
                            python3-dev \
                            python3-numpy \
                            python3-setuptools \
                            swig \
                            openjdk-8-jdk \
                            build-essential \
                            git \
                            libgconf-2-4 \
                            wget \
                            cmake \
                            gfortran \
                            coinor-libipopt-dev \
                            libcolpack-dev \
                            pkg-config \
                            autoconf libtool libtool-bin \
                            ant
RUN wget -q https://download.netbeans.org/netbeans/8.2/final/bundles/netbeans-8.2-javase-linux.sh && chmod +x netbeans-8.2-javase-linux.sh && ./netbeans-8.2-javase-linux.sh --jdkhome /usr/lib/jvm/java-8-openjdk-amd64 --silent && echo $(ls /usr/local/netbeans-8.2)
RUN git clone https://github.com/opensim-org/opensim-gui.git && cd opensim-gui && git clone https://github.com/opensim-org/opensim-core.git
RUN cd opensim-gui && mkdir build_deps && cd build_deps \
    && cmake ../opensim-core/dependencies -DSUPERBUILD_ezc3d:BOOL=on -DCMAKE_INSTALL_PREFIX="/root/opensim_dependencies_install" -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    && cmake . -LAH && make -j4 && cd .. \
    && mkdir build_core && cd build_core \
    && cmake ../opensim-core -DOPENSIM_DEPENDENCIES_DIR="/root/opensim_dependencies_install" -DBUILD_JAVA_WRAPPING=on -DBUILD_PYTHON_WRAPPING=on -DOPENSIM_C3D_PARSER=ezc3d -DBUILD_TESTING=off -DCMAKE_INSTALL_PREFIX="/root/opensim-core-install" -DOPENSIM_INSTALL_UNIX_FHS=OFF -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    && cmake . -LAH && make -j4 && make install && cd .. \
    && git submodule update --init --recursive -- opensim-models opensim-visualizer Gui/opensim/threejs \
    && mkdir build && cd build \
    && cmake ../ -DCMAKE_PREFIX_PATH="/root/opensim-core-install" -DANT_ARGS="-Dnbplatform.default.netbeans.dest.dir=/usr/local/netbeans-8.2;-Dnbplatform.default.harness.dir=/usr/local/netbeans-8.2/harness" && make CopyOpenSimCore && make PrepareInstaller