# DeputyChallenge

![](https://imgur.com/3Mw25WU)

This is my (Orrie Shannon's) submission to the Deputy Coding Challenge.


<b>Part 1</b>


Time Taken: ~4 hours  
This almost doubles the suggested time. This is because I chose to include several things that were not necessary to complete the challenge, but I think do a better job demonstrating my abilities to write scalable, maintainable code that is more likely to be found in production applications serving millions of users. 
  
Some examples of these extras are:  
-MVVM structure  
-Using a recylerview instead of simple listview, with custom views inside  
-Using Dagger for dependency injection  


I only encountered one issue with the apis returning a 403. It turned out that including a `/` at the beginning of the path in the retrofit interfaces was somehow removing the `dmc` part of the base url which I discovered after adding OkHttp logging to the api calls. 

Libraries used:  
Mockito to mock dependencies for unit testing: https://site.mockito.org/  
Nhaarman mockito for helper functions to use Mockito in Kotlin: https://github.com/nhaarman/mockito-kotlin  
Timber for easy logging (no more TAGs everywhere): https://github.com/JakeWharton/timber  
RxJava and RxAndroid for simplify asynchronous code and scheduling: https://github.com/ReactiveX/RxAndroid  
RxBinding to easily use RxJava with Android's native widgets: https://github.com/JakeWharton/RxBinding  
Retrofit along with RxJava adapters and gson converter for making network calls and automatically converting them to observables emitting the correct data classes: https://square.github.io/retrofit/  
Dagger for dependency injection: https://google.github.io/dagger/android.html
Glide for image loading: https://github.com/bumptech/glide
OkHttp logging interceptor for debugging network calls: https://github.com/bumptech/glide


<b>Part 2</b>

1. What is important to you in your career?  
There are two main things that are important to me. Firstly, that I am working on the solution to a problem that not only interests me but also has a positive effect on large number of people. Secondly, that I am continually challenged and presented with opportunities to grow and learn. 

2. What excites you about Deputy and the opportunity?
Again, there are two main reasons why I am excited about working at Deputy. Firstly, it fulfills what I am looking for from question 1. Having worked in shift work in the past, it is a relatable problem Deputy is trying to solve that I am very much interested in, and also solving this problem can potentially positively benefit an enormous number of people. Secondly, I have worked at a similar sized company before and I think it is a really fun period to be a part of. At Deputy’s size with funding secured, it is a stable place to work with many processes already in place to make things run efficiently, however it is still small enough things get shipped quickly and my work can have a huge impact. 

3. What would you like us to know about you? 
With almost 8 years in Android development I have a very strong technical background and having worked for companies serving millions of customers I have a lot of experience building solutions that scale. Additionally, having worked as team lead for several years, my communication skills are excellent. Whether this is giving feedback to peers, working with product managers on the next feature, or giving updates to the entire company. I’m also very social and enjoy organizing and participating in after work events to get to know my peers better. 


