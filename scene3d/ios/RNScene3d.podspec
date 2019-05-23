
Pod::Spec.new do |s|
  s.name         = "RNScene3d"
  s.version      = "1.0.0"
  s.summary      = "RNScene3d"
  s.description  = <<-DESC
                  RNScene3d
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNScene3d.git", :tag => "master" }
  s.source_files  = "RNScene3d/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  