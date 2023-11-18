FROM mcr.microsoft.com/dotnet/aspnet:6.0

RUN mkdir -p /app/DOTNET_CLI_HOME

# Set the working directory inside the container
WORKDIR /app

# Copy the published files (assuming they are in the 'public' directory)
COPY ./public .

# Expose the port the app will run on
EXPOSE 80
EXPOSE 443

# Setup non root user container image
ENV DOTNET_CLI_HOME="/app/DOTNET_CLI_HOME"

RUN adduser --disabled-password --system --uid 1000 --home /app --gecos "" dotnetuser && chown -R dotnetuser /app

USER dotnetuser

# Define the command to run the application
ENTRYPOINT ["dotnet", "App.dll"]