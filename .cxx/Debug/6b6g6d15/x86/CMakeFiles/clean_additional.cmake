# Additional clean files
cmake_minimum_required(VERSION 3.16)

if("${CONFIG}" STREQUAL "" OR "${CONFIG}" STREQUAL "Debug")
  file(REMOVE_RECURSE
  "../../../../src/main/java/nl/igorski/mwengine/core/MWEngineCore.java"
  "../../../../src/main/java/nl/igorski/mwengine/core/MWEngineCoreJNI.java"
  "../../../../src/main/java/nl/igorski/mwengine/core/mwengineJAVA_wrap.cxx"
  )
endif()
