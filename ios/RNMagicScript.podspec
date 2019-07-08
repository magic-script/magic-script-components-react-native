
Pod::Spec.new do |s|
  s.name         = "RNMagicScript"
  s.version      = "0.1.0"
  s.summary      = "RNMagicScript"
  s.description  = "Magic script components for React Native."
  s.homepage     = "https://www.magicscript.org/"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "author@domain.cn" }
  s.platform     = :ios, "11.0"
  s.source       = { :git => "git@github.com:magic-script/react-native-magic-script.git", :tag => "master" }
  s.source_files  = "RNMagicScript/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  #s.dependency "others"

end

  