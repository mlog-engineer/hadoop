package org.apache.hadoop.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.http.client.AccessKeyHttpFSFileSystem;

import org.apache.hadoop.fs.http.client.HttpFSFileSystem;
import org.junit.Test;

public class TestHttpFs {

  @Test
  public void test() throws Exception {
    Configuration conf = new Configuration();
    conf.set("fs.defaultFS","webhdfs://127.0.0.1:8080");
    conf.set("httpfs.authentication.accesskey","abc");
    conf.set("fs.webhdfs.impl",HttpFSFileSystem.class.getName());
    conf.set("httpfs.authentication.type","accesskey");
    FileSystem fs = FileSystem.get(conf);
    System.out.println(fs.getClass().getName());
    Path home = fs.getHomeDirectory();
    System.out.println(home.toString());
    for(int i=0;i<100;i++) {
      FileStatus[] status = fs.listStatus(new Path("/Users/tiger"));
      System.out.println(status.length);
      for (FileStatus f : status) {
        System.out.println(f.toString());
      }
    }
  }

}
