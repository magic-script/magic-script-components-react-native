
Pod::Spec.new do |s|
  s.name         = "RNMagicScript"
  s.version      = "0.1.0"
  s.summary      = "RNMagicScript"
  s.description  = "MagicScript Components renderer for React Native platform."
  s.homepage     = "https://www.magicscript.org/"
  s.license    = { :type => "Apache License, Version 2.0", :file => "../LICENSE" }
  s.author       =  "Magic Leap"
  s.platform     = :ios, "12.0"
  s.source       = { :git => "git@github.com:magic-script/magic-script-components-react-native.git", :tag => "master" }
  s.source_files  = ["RNMagicScript/**/*.{h,m,swift}"]
  s.resource_bundles = {
    "RNMagicScriptResources" => ["**/*.{xcassets}", "**/Shaders/*.{txt}"]
  }
  s.requires_arc = true

  s.dependency 'React', '0.60.5'
  s.dependency 'GLTFSceneKit', '0.1.3'
  s.dependency 'GrowingTextView', '0.7.2'
  s.dependency 'ChromaColorPicker', '1.8.0'

end
