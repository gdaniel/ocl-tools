# This is required because Xatkit is not yet on Maven Central or similar

# Print a message
e() {
    echo -e "$1"
}

main() {
	
	# Do not print the build log, it is already available in the Xatkit build
    e "Building Unpublished Dependencies"
    cd /tmp
    git clone https://github.com/unveiling/unveiling-commons.git > /dev/null
    cd unveiling-commons
    mvn clean install -N -DskipTests > /dev/null
}

main