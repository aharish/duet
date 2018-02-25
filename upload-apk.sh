#create a new directory that will contain out generated apk
  mkdir $HOME/buildApk/ 
  #copy generated apk from build folder to the folder just created
  cp -R app/build/outputs/apk/app-debug.apk $HOME/android/
  #go to home and setup git  
  cd $HOME
  git config --global user.email "useremail@domain.com"
  git config --global user.name "Your Name" 
  #clone the repository in the buildApk folder
  git clone --quiet --branch=master  https://user-name:$GITHUB_API_KEY@github.com/user-name/repo-name  master > /dev/null
  #go into directory and copy data we're interested
  cd master  cp -Rf $HOME/android/* .
  #add, commit and push files
  git add -f .
  git remote rm origin
  git remote add origin https://user-name:$GITHUB_API_KEY@github.com/user-name/repo-name.git
  git add -f .
  git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci] "
  git push -fq origin master > /dev/null
  echo -e "Done\n"
