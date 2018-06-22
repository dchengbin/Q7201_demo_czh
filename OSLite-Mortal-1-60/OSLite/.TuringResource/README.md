##  .TuringResource文件夹使用说明

### 简介
.TuringResource 是 OSLite 的重要组成部分，其中包含两类文件：
   - 资源文件   
    >  资源文件包含两类：一类是OSLite实现的默认function所用到的资源；另一类是为方便开发者理解所添加的示例资源，这部分资源通常需要开发者按照自己的需求替换

   - 配置文件
   > 配置文件也包含两类：一类配置文件是function的必备数据，只有这些配置文件存在且正确，该function才能正常的执行（eg.multimodal 下的配置文件为多模态输出的必备数据，如果缺失将导致多模态数据生成不了，无法完成正确的多模态输出）；另一类配置文件的作用是配置框架的运行时行为、一些附加功能的加载位置（从内部加载还是从独立apk加载）

### 文件说明
- asr/   
  该文件夹下放置与asr识别有关的配置文件
  -  baidu_speech_grammar.bsg
  > 百度离线语音识别的语法文件，若用户需要使用OS Lite的默认离线识别功能，需在 [ 百度语义识别服务 ](http://yuyin.baidu.com/asr)配置并生成自己的语法文件
- audio/   
    该文件夹下放置一些音频资源
    - asr_prompt_tone.mp3
    > asr 开启前提示音，用户可根据需要替换为自己的提示音文件
    - sample_song.mp3
    > sample code demo 中 music 场景demo 用到的一首示例音乐，用户可不用关心
-  gif/  
    该文件夹下放置默认的表情function 需要用到的gif图片资源，默认不提供，用户如有需要联系图灵获取


- motor/  
    在虚拟机器人版本中，虚拟机器人的显示目前以mp4做为人物形象的视频资源，默认提供的几个mp4文件作为示例仅供参考，用户需提供自己IP的人物形象视频。


- multimodal/  
    该文件夹下主要放置的是与多模态输出相关的配置文件  

| 文件名 | 文件说明 |  
| --------- | ------------- |
| actions.xml | 对话返回的情绪值与动作code的对应配置文件|
| actions_control.xml | 语音动作指令对应的动作code配置文件|
|actions_threshold.xml|当情绪编码未返回时，根据返回的愉悦度划分不同等级配置的动作code|
|avatar_actions_filename.xml| 虚拟机器人动作code与机器人动作资源文件名对应关系配置|
|emotions.xml|对话返回的情绪值与表情名称的对应配置文件|
|emotions_eyecode.xml|实体机器人情绪名称与眼部动作code的配置文件|
|emotions_threshold.xml|当情绪编码未返回时，根据返回的愉悦度划分不同等级配置的情绪名称|

- string/   
资源字符串（string 目录下的xml文件中定义），格式例如
    <!-- lang:java -->
    ```xml
  <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="robot_sleep">Robot Sleep.</string>
        <string name="robot_wakeup">Robot Wakeup.</string>
        <string name="user_interrupt">I was interrupted.</string>

        <string name="network_disconnected">Network disconnected. Please use client connect network.</string>
        <string name="network_connected">Network connected.</string>
    </resources>
    ```
    开发者可通过修改这些字符串资源自定义自己的对应提示语。

- plugin/
 >插件（plugin目录），放在在该目录下的apk文件将被扫描， 在其中包含的function和scenario将被以插件的方式添加入框架中

- system   
    系统配置（system目录），放置一些影响系统运行行为的配置文件。
    - similar_chat_scenario_keys.xml
    该配置主要配置一些类似聊天的场景，使的这些场景无需独立开发即可进行正常的一问一答，配置示例如下：
    ``` xml
    <resources>
        <string-array name="keys">
            <item>os.sys.wiki</item>
        </string-array>
    </resources>    
    ```
    - system_properties.xml
    > 目前其中的配置项用来配置相应的功能使用OSLite 内部服务（APP包名必须与图灵保持一致方可使用）还是使用队里APK加载（APP包名无关）

### 资源文件夹使用
1. 需将此文件夹（.TuringResource），拷贝到设备的sdcard目录下使用，否则框架将找无法加载对应的资源。
2. 文件夹下个资源的自定义和配置文件的修改，可参考个文件内的说明。
3. 配置文件修改后通常需要重新启动OS Lite，但是这点不是必须的。
