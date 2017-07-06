for i in `find src/test/groovy -name '*Spec.groovy'`;
do
    echo $i
    egrep "def|given|when|then|and:" $i |grep -v private
done
