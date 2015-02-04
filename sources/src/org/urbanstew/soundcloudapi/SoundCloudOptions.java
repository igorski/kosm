/*
 *  Copyright 2009 urbanSTEW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.urbanstew.soundcloudapi;

public class SoundCloudOptions
{
	public SoundCloudOptions()
	{}
	
	public SoundCloudOptions(SoundCloudAPI.SoundCloudSystem system)
	{
		this.system = system;
	}
	
	public SoundCloudOptions(SoundCloudAPI.SoundCloudSystem system, SoundCloudAPI.OAuthVersion version)
	{
		this.system = system;
		this.version = version;
	}
	
	public SoundCloudOptions with(SoundCloudAPI.OAuthVersion newVersion)
	{	return new SoundCloudOptions(system, newVersion); }
	
	public SoundCloudOptions with(SoundCloudAPI.SoundCloudSystem newSystem)
	{	return new SoundCloudOptions(newSystem, version); }

	public SoundCloudAPI.OAuthVersion version = SoundCloudAPI.OAuthVersion.V1_0_A;
	public SoundCloudAPI.SoundCloudSystem system = SoundCloudAPI.SoundCloudSystem.PRODUCTION;
}
